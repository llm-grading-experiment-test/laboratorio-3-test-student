#!/usr/bin/env python3
"""
Grade a single student submission using an LLM via GitHub Models API.

This script runs inside a GitHub Action at the lab's deadline.
It packages the student's source code, renders the prompt with the lab's
rubric and specification, calls GitHub Models (free, uses GITHUB_TOKEN),
and saves the evaluation JSON.

GitHub Models API:
    - Endpoint: https://models.github.ai/inference/chat/completions
    - Auth: Bearer $GITHUB_TOKEN (native to GitHub Actions)
    - Permission required: models: read
    - No external API key needed

Environment variables (set by the GitHub Action):
    GITHUB_TOKEN: Native Actions token (used for GitHub Models auth)
    GEMINI_API_KEY: Optional API key for Google Gemini free tier
    LLM_MODEL: Model name (default: gemini-2.5-flash)
    RUBRIC_PATH: Path to rubric.json
    LAB_SPEC_PATH: Path to lab_specification.md
    GRADING_REFERENCE_PATH: Path to grading_reference.md (optional)
    SOURCE_ROOTS: Comma-separated source directories (default: src)
    SOURCE_EXTENSIONS: Comma-separated file extensions (default: .java)
    TEMPERATURE: LLM temperature (default: 0)
"""

import json
import os
import re
import sys
import time
from pathlib import Path
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from urllib.parse import urlencode

# ---------------------------------------------------------------------------
# Source collection & packaging
# ---------------------------------------------------------------------------

def collect_source_files(repo_root, source_roots, extensions):
    """Collect all source files from the repository."""
    files = {}
    for root_name in source_roots:
        root_path = repo_root / root_name
        if not root_path.exists():
            continue
        for ext in extensions:
            for file_path in root_path.rglob(f"*{ext}"):
                relative = file_path.relative_to(repo_root)
                content = file_path.read_text(encoding="utf-8", errors="replace")
                # Mask @author lines
                lines = content.split("\n")
                masked = []
                for line in lines:
                    stripped = line.strip().lower()
                    if stripped.startswith("* @author") or stripped.startswith("@author"):
                        masked.append(line.split("@author")[0] + "@author [ANONIMIZADO]")
                    else:
                        masked.append(line)
                files[str(relative)] = "\n".join(masked)
    return files


def package_submission(files):
    """Package source files into a markdown document for the prompt."""
    lines = ["# Código-fonte da submissão\n"]
    for path, content in sorted(files.items()):
        ext = Path(path).suffix.lstrip(".")
        lines.append(f"## `{path}`\n")
        lines.append(f"```{ext}\n{content}\n```\n")
    return "\n".join(lines)


# ---------------------------------------------------------------------------
# Prompt rendering
# ---------------------------------------------------------------------------

def render_prompt(template, submission_package, rubric_json, lab_spec,
                  grading_reference, output_schema,
                  evaluation_principles="", feedback_style="",
                  gold_standard_calibration="", starter_scaffold="",
                  calibration_checks="", experiment_metadata=""):
    """Render the prompt template with all components."""
    replacements = {
        "{{SUBMISSION_PACKAGE}}": submission_package,
        "{{RUBRIC_JSON}}": rubric_json,
        "{{LAB_SPEC_TEXT}}": lab_spec,
        "{{GRADING_SHEET_TEXT}}": grading_reference,
        "{{OUTPUT_SCHEMA_JSON}}": output_schema,
        "{{CORE_EVALUATION_TEXT}}": evaluation_principles or "(não configurado)",
        "{{COMMENT_STYLE_TEXT}}": feedback_style or "(não configurado)",
        "{{GOLD_STANDARD_STYLE_TEXT}}": gold_standard_calibration or "(não configurado)",
        "{{STARTER_SCAFFOLD_TEXT}}": starter_scaffold or "(não configurado)",
        "{{FINAL_CALIBRATION_CHECKS_TEXT}}": calibration_checks or "(não configurado)",
        "{{EXPERIMENT_METADATA}}": experiment_metadata or "(não configurado)",
    }
    result = template
    for key, value in replacements.items():
        result = result.replace(key, value)
    return result


# ---------------------------------------------------------------------------
# API Calls: GitHub Models & Gemini
# ---------------------------------------------------------------------------

GITHUB_MODELS_ENDPOINT = "https://models.github.ai/inference/chat/completions"


def call_github_models(prompt, token, model, temperature, max_retries=3):
    """Call GitHub Models API using the native GITHUB_TOKEN."""
    payload = json.dumps({
        "messages": [
            {"role": "system", "content": "Você é um professor de Ciência da Computação avaliando um laboratório de programação. Responda APENAS com JSON válido, sem markdown."},
            {"role": "user", "content": prompt}
        ],
        "model": model,
        "temperature": temperature,
        "max_tokens": 4096,
    }).encode("utf-8")

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}",
    }

    for attempt in range(max_retries + 1):
        try:
            req = Request(GITHUB_MODELS_ENDPOINT, data=payload,
                          headers=headers, method="POST")
            with urlopen(req, timeout=120) as response:
                result = json.loads(response.read().decode("utf-8"))
            return result["choices"][0]["message"]["content"]
        except HTTPError as e:
            if e.code in {429, 503} and attempt < max_retries:
                wait = min(2 ** attempt * 5, 60)
                print(f"  Rate limited ({e.code}), waiting {wait}s "
                      f"(attempt {attempt + 1}/{max_retries})")
                time.sleep(wait)
                continue
            body = e.read().decode("utf-8", errors="replace")
            print(f"ERROR: GitHub Models API returned {e.code}: {body}",
                  file=sys.stderr)
            raise


def call_gemini(prompt, api_key, model, temperature, max_retries=3):
    """Call Google Gemini API."""
    query = urlencode({"key": api_key})
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?{query}"
    
    payload = json.dumps({
        "contents": [
            {
                "role": "user",
                "parts": [{"text": prompt}]
            }
        ],
        "systemInstruction": {
            "parts": [{"text": "Você é um professor de Ciência da Computação avaliando um laboratório de programação. Responda APENAS com JSON válido, sem markdown."}]
        },
        "generationConfig": {
            "temperature": temperature,
            "responseMimeType": "application/json",
        },
    }).encode("utf-8")

    request = Request(
        url,
        data=payload,
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    for attempt in range(max_retries + 1):
        try:
            with urlopen(request, timeout=120) as response:
                result = json.loads(response.read().decode("utf-8"))
            
            # Extract text from Gemini response
            parts = []
            for candidate in result.get("candidates", []):
                for part in candidate.get("content", {}).get("parts", []):
                    if text := part.get("text"):
                        parts.append(text)
            return "".join(parts)

        except HTTPError as e:
            body = e.read().decode("utf-8", errors="replace")
            if e.code in {429, 503} and attempt < max_retries:
                try:
                    error_json = json.loads(body)
                    wait = 10
                    for detail in error_json.get("error", {}).get("details", []):
                        if delay_str := detail.get("retryDelay"):
                            wait = float(delay_str.rstrip("s"))
                except:
                    wait = min(2 ** attempt * 5, 60)
                
                print(f"  Rate limited ({e.code}), waiting {wait}s "
                      f"(attempt {attempt + 1}/{max_retries})")
                time.sleep(wait)
                continue
            
            print(f"ERROR: Gemini API returned {e.code}: {body}", file=sys.stderr)
            raise



# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    # Read environment
    token = os.environ.get("GITHUB_TOKEN", "")
    gemini_key = os.environ.get("GEMINI_API_KEY", "")
    model = os.environ.get("LLM_MODEL", "gemini-2.5-flash")
    rubric_path = os.environ.get("RUBRIC_PATH", "rubric.json")
    lab_spec_path = os.environ.get("LAB_SPEC_PATH", "lab_specification.md")
    grading_ref_path = os.environ.get("GRADING_REFERENCE_PATH", "")
    source_roots = os.environ.get("SOURCE_ROOTS", "src").split(",")
    source_extensions = os.environ.get("SOURCE_EXTENSIONS", ".java").split(",")
    temperature = float(os.environ.get("TEMPERATURE", "0"))

    if not token:
        print("ERROR: GITHUB_TOKEN not set. This script requires the "
              "native GitHub Actions token.", file=sys.stderr)
        sys.exit(1)
        
    is_gemini = model.startswith("gemini")
    if is_gemini and not gemini_key:
        print("ERROR: GEMINI_API_KEY not set. This is required for Gemini models.", file=sys.stderr)
        sys.exit(1)

    repo_root = Path(os.environ.get("GITHUB_WORKSPACE", "."))

    # Locate prompt template and schema
    # Check in the repo first, then in the integration directory
    script_dir = Path(__file__).parent
    possible_roots = [repo_root, script_dir.parent.parent.parent]
    template_path = None
    schema_path = None

    for root in possible_roots:
        t = root / "prompt" / "template.md"
        s = root / "prompt" / "output_schema.json"
        if t.exists() and template_path is None:
            template_path = t
        if s.exists() and schema_path is None:
            schema_path = s

    if not template_path:
        print("ERROR: prompt/template.md not found", file=sys.stderr)
        sys.exit(1)

    # Collect source files
    files = collect_source_files(repo_root, source_roots, source_extensions)
    if not files:
        print("WARNING: No source files found", file=sys.stderr)

    # Package submission
    submission_package = package_submission(files)

    # Read prompt components
    template = template_path.read_text(encoding="utf-8")
    output_schema = schema_path.read_text(encoding="utf-8") if schema_path else "{}"
    rubric_json = Path(rubric_path).read_text(encoding="utf-8") if Path(rubric_path).exists() else "{}"
    lab_spec = Path(lab_spec_path).read_text(encoding="utf-8") if Path(lab_spec_path).exists() else ""
    grading_reference = ""
    if grading_ref_path and Path(grading_ref_path).exists():
        grading_reference = Path(grading_ref_path).read_text(encoding="utf-8")

    # Load optional protocol files from repo
    def load_optional(rel_path):
        for root in possible_roots:
            p = root / rel_path
            if p.exists():
                return p.read_text(encoding="utf-8")
        return ""

    evaluation_principles = load_optional("protocol/evaluation_principles.md")
    feedback_style = load_optional("protocol/feedback_style_guide.md")
    gold_standard_calibration = load_optional("gold_standard_calibration.md")
    starter_scaffold = load_optional("starter_scaffold.md")
    calibration_checks = load_optional("calibration_checks.md")

    # Render prompt
    prompt = render_prompt(
        template, submission_package, rubric_json, lab_spec,
        grading_reference, output_schema,
        evaluation_principles=evaluation_principles,
        feedback_style=feedback_style,
        gold_standard_calibration=gold_standard_calibration,
        starter_scaffold=starter_scaffold,
        calibration_checks=calibration_checks,
    )

    # Call API
    print(f"Calling LLM ({model}, temperature={temperature})...")
    print(f"Prompt size: {len(prompt)} characters")
    
    if is_gemini:
        response_text = call_gemini(prompt, gemini_key, model, temperature)
    else:
        response_text = call_github_models(prompt, token, model, temperature)

    # Parse response
    clean_text = response_text.strip()
    if clean_text.startswith("```"):
        lines = clean_text.split("\n")
        clean_text = "\n".join(lines[1:-1])

    try:
        evaluation = json.loads(clean_text)
    except json.JSONDecodeError as e:
        print(f"ERROR: Failed to parse LLM response as JSON: {e}",
              file=sys.stderr)
        Path("llm_response_raw.txt").write_text(response_text, encoding="utf-8")
        print("Raw response saved to llm_response_raw.txt", file=sys.stderr)
        sys.exit(1)

    # Save evaluation
    output_path = Path("evaluation.json")
    output_path.write_text(json.dumps(evaluation, indent=2, ensure_ascii=False),
                           encoding="utf-8")

    total_score = evaluation.get("total_score", "N/A")
    print(f"✅ Evaluation saved to {output_path}")
    print(f"   Total score: {total_score}")

    # Write to GITHUB_OUTPUT
    github_output = os.environ.get("GITHUB_OUTPUT")
    if github_output:
        with open(github_output, "a") as f:
            f.write(f"evaluation_json={output_path}\n")
            f.write(f"total_score={total_score}\n")


if __name__ == "__main__":
    main()

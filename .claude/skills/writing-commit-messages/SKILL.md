---
name: writing-commit-messages
description: Use when creating a git commit in this project - covers checking repo history before committing, conventional commit message format enforced by commitlint, and what to never include
---

# Writing Commit Messages

## Overview

Commits in this project are linted by commitlint (conventional commits). Messages must be short, single-line, and follow the type(scope) format. Never add co-author trailers.

## Before Committing — Always

Run these first, every time:

```bash
git status              # what's staged/unstaged/untracked
git diff                # unstaged changes
git diff --staged       # staged changes
git log --oneline -10   # recent commit style, scopes used
```

Use `git log` to match scopes/wording conventions already used in the project (e.g. `feat(bank): ...`, `fix(admin): ...`).

## Message Format

```
type(scope): subject
```

- One line only. No body, no footer, no blank-line-separated paragraphs.
- `scope` is optional but use one if recent commits in that area use one (check `git log`).

### Rules (from commitlint.config.cjs)

```js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [2, 'always', [
      'feat', 'fix', 'docs', 'style', 'refactor',
      'perf', 'test', 'chore', 'revert', 'ci',
    ]],
    'type-case': [2, 'always', 'lower-case'],
    'subject-case': [2, 'always', 'lower-case'],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'header-max-length': [2, 'always', 100],
    'scope-case': [2, 'always', 'lower-case'],
  },
};
```

In short: `type` and `subject` lower-case, `type` from the list above, no trailing period, no empty subject, whole header ≤ 100 chars, `scope` lower-case.

### Type meanings

* `feat`: new feature
* `fix`: bug fix
* `docs`: documentation changes
* `style`: code formatting changes that do not affect logic
* `refactor`: code refactoring that is neither a feature nor a bug fix
* `perf`: performance improvements
* `test`: adding or updating tests
* `chore`: build process, tooling, or dependency changes
* `revert`: reverting a previous commit
* `ci`: CI/CD configuration changes


## Never Do This

- **Never** add a `Co-Authored-By:` line or any "Generated with Claude" trailer.
- Never write a multi-line commit body — keep it to one line.
- Never use uppercase in `type`, `scope`, or the start of `subject`.
- Never end the subject with a period.

## If Unclear

If you're not sure what type/scope fits, or the change mixes multiple concerns (e.g. both a fix and a refactor), stop and ask the user how they want it framed/split — don't guess.

## Example

```
fix(bank): correct balance calculation for topup
feat(admin): add export button to transaction list
chore: bump laravel to 12.5
```
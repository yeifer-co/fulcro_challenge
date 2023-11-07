## Commits convention
Commit types

- 📃: Documentation (Involves any changes to the documentation, including improve docstrings in codebase, but excluding comments lines and blocks)
- 🌟: Feature (Changes that add new functionality or improve existing one without error corrections)
- 🐞: Bug fix (Error corrections and bug fixes thats not involve new functionality)
- 🎨: Style (Changes in the codebase that do not affect the meaning of the code, such as white-space, tabs, formatting, comments)
- 🔁: Refactor (Any code change that does not fix a bug or add a feature but improves the codebase readability, such as renaming, code reorganization, optimization) 
- 🔧: Config (Operations related to configuration or environment files that do not involve code changes)

Commit scopes

- 📦: Project
- 🧩: Codebase
- 🧐: Testing

Commit structure

```
<type>(<[optional]scope>|...|<[optional]scope>): <subject>
```

Example commit

```
📃: Add commit conventions to README.md
🐞(🧩): Fix bug in the codebase
🔁(🧩|📦): Use main to execute script and be able to run inbuild inteliJ with deps
```

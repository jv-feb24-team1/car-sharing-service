# Documentation for contributing

### Table of Contents
- [Pull Request Rules](#pull-request-rules)
- [Commit Rules](#commit-rules)
- [Merge & Squash Rules](#merge--squash-rules)

## Pull Request Rules
1. **Branching**: Create a new branch for each feature or bug fix.
   ```
   git checkout -b your-feature-name
    ```
2. **Description**: Provide a clear description of the changes and the problem they solve.
3. **Keyword**: The keyword should be the first element in the name, indicating the type of change 
   the PR introduces. 
    ```
   Add New entity user added
    ```
   **Here's a table of standard keywords and their meanings:**

     | Type                  | Description                                                             |
     |-----------------------|-------------------------------------------------------------------------|
     | **Add**               | Create a capability (e.g., feature, test, dependency).                  |
     | **Remove**            | Remove a capability (e.g., feature, test, dependency).                  |
     | **Refactor / Update** | Refactor or update existing code.                                       |
     | **Bugfix**            | Fix an issue (e.g., bug, typo, accident, misstatement).                 |
     | **Bump**              | Increase the version of something (e.g., dependency).                   |
     | **Build**             | Change only the build process, tooling, or infrastructure.              |
     | **Revert**            | Reverting a previous commit.                                            |
4. **Review**: Ensure that you get approval from two reviewers before merging.

## Commit Rules
1. **Message**: Use clear, concise commit messages.
   * Short summary (**50 characters or less**)
   * Detailed description (optional)
2. **Present Tense**: Write messages in the present tense (e.g., "**Fix bug**" not "**Fixed bug**").
3. **Imperative Mood**: Use the imperative mood (e.g., "**Add feature**" not "**Adds feature**").

## Merge & Squash Rules
1. **Merge Strategy**: Use "Squash and merge" to keep a clean commit history.
2. **Description**: Update the description to provide a clear summary of all changes made in the commits.

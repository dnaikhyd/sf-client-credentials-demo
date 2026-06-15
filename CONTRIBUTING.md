# Contributing to Salesforce OAuth Playground

First off, thank you for considering contributing to Salesforce OAuth Playground! 🎉

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Guidelines](#coding-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)

## 🤝 Code of Conduct

This project and everyone participating in it is governed by our commitment to providing a welcoming and inspiring community for all. Please be respectful and constructive in your interactions.

## 🎯 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce** the issue
- **Expected behavior** vs actual behavior
- **Environment details** (Java version, OS, Salesforce edition)
- **Error messages** or logs (with sensitive data removed)
- **Screenshots** if applicable

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear title and description**
- **Use case** - why is this enhancement useful?
- **Proposed solution** - how would you implement it?
- **Alternatives considered**
- **Additional context** or screenshots

### Contributing Code

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes**
4. **Test thoroughly**
5. **Commit your changes** (see commit message guidelines below)
6. **Push to your fork** (`git push origin feature/amazing-feature`)
7. **Open a Pull Request**

## 🛠️ Development Setup

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git
- A Salesforce Developer account

### Setup Steps

1. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/sf-client-credentials-demo.git
   cd sf-client-credentials-demo
   ```

2. Set up environment:
   ```bash
   cp .env.template .env
   # Edit .env with your Salesforce credentials
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Test examples:
   ```bash
   cd examples
   javac SimpleConnectionTest.java
   java SimpleConnectionTest --debug
   ```

## 📝 Coding Guidelines

### Java Code Style

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 120 characters
- **Naming conventions**:
  - Classes: `PascalCase`
  - Methods: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Variables: `camelCase`

### Code Quality

- **Comments**: Add JavaDoc for public methods and classes
- **Error handling**: Use try-catch blocks appropriately
- **Logging**: Use consistent logging patterns
- **Security**: Never commit credentials or sensitive data
- **Debug mode**: Add `--debug` flag support for new features

### Example Code Style

```java
/**
 * Authenticates with Salesforce using OAuth 2.0 Client Credentials Flow.
 * 
 * @param clientId The OAuth consumer key
 * @param clientSecret The OAuth consumer secret
 * @return Authentication response with access token
 * @throws IOException If authentication fails
 */
private static AuthResponse authenticate(String clientId, String clientSecret) throws IOException {
    if (debugMode) {
        System.out.println("[DEBUG] Starting authentication...");
    }
    
    // Implementation here
}
```

## 💬 Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples

```
feat(examples): add bulk delete example

Add new example showing how to delete multiple records
using Salesforce Composite API.

Closes #123
```

```
fix(auth): handle token expiration correctly

Previously, expired tokens caused authentication failures.
Now automatically refreshes tokens when expired.

Fixes #456
```

## 🔄 Pull Request Process

### Before Submitting

1. **Update documentation** if you've changed functionality
2. **Add tests** for new features
3. **Ensure all tests pass**: `mvn test`
4. **Update CHANGELOG.md** with your changes
5. **Check code style** matches project conventions

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe how you tested your changes

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests added/updated
- [ ] All tests pass
```

### Review Process

1. At least one maintainer will review your PR
2. Address any requested changes
3. Once approved, a maintainer will merge your PR
4. Your contribution will be included in the next release!

## 🎓 First Time Contributors

New to open source? Here are some good first issues:

- Documentation improvements
- Adding code comments
- Fixing typos
- Adding examples
- Improving error messages

Look for issues labeled `good first issue` or `help wanted`.

## 📚 Additional Resources

- [Salesforce OAuth Documentation](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flows.htm)
- [Salesforce REST API Guide](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/)
- [Java Coding Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

## 🙏 Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes
- Project documentation

## 📧 Questions?

Feel free to:
- Open an issue for questions
- Start a discussion in GitHub Discussions
- Reach out to maintainers

---

Thank you for contributing! 🚀
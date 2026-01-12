# NewsAppp - Architecture Madness 🎪

> **Warning**: This project is intentionally over-engineered for educational and experimental purposes. Read at your own risk!

## About

This project should probably be called “Architecture Madness”. It’s my personal excuse to over-engineer a News app, and I’m going to push every concept to its extreme version; not because it’s the smartest thing to do, but because it’s fun (because we all sometimes love complex things—even when they don’t bring much value). And since I can’t do this at work, I’m doing it here. 

Read the code at your own risk. You can still learn from these samples and steal what’s useful, but please don’t go “I did it because it does so here”. Also feel free to imitate what you see on big companies’ engineering blogs… you know, despite the tiny detail that you’re not them (and neither am I).”

## Project Structure

This repository demonstrates different architectural approaches to building the same News application:

### MVVM Implementation
A classic MVVM (Model-View-ViewModel) approach with "modern Android best practices". See [mvvm/README.md](mvvm/README.md) for details.

*More architectural patterns coming soon...*

## Learning Goals

- Understand the complexity trade-offs in different architectures
- See patterns pushed to their extremes
- Learn when simpler is better
- Pick useful concepts that fit your actual needs

## What You Can Learn

- State management patterns
- Testability strategies
- Dependency injection approaches
- And much more...

## What NOT to Learn

- Don't treat complexity as a badge of honor
- Don't over-engineer your production apps
- Don't cargo-cult patterns from big tech without understanding context
- Don't sacrifice maintainability for "purity"

## 🤝 Contributing

Feel free to open issues or PRs if you want to:
- Add new architectural patterns
- Improve existing implementations
- Fix bugs or improve documentation

## Setup

1. Create an account on [newsapi.org](https://newsapi.org/)
2. Get your API key from the dashboard
3. Add your API key to the `local.properties` file in the project root:

```properties
NEWS_API_KEY=your_api_key_here
```

## Opening Projects in Android Studio

Use the `studiow` script to quickly open subprojects in Android Studio:

```bash
# Open the MVVM project
./studiow mvvm

# Open the entire workspace
./studiow all

# Clean build caches before opening
./studiow --clean mvvm

# List all available projects
./studiow --list

# Show help
./studiow --help
```

You can also open any project by its path: `./studiow ./path/to/project`

## License

This project is open source and available for educational purposes.
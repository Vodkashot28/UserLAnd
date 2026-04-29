# Contributing to UserLAnd-Next

Thanks for throwing some effort into helping us improve our project!

This is a set of guidelines for contributing to the UserLAnd-Next Android application.
These are intended as just that: guidelines, so use your best judgement when submitting contributions.

All contributions must follow the UserLAnd-Next [Code of Conduct](https://github.com/Vodkashot28/UserLAnd/blob/main/CODE_OF_CONDUCT.md).

## Connect with us
Please talk with us here in the form of a PR or an issue.

## Architecture
We follow the MVVM-C architecture in UserLAnd-Next. UI updates should exist exclusively within XML and be inflated exclusively from
view-controllers like activities and fragments. Business logic should be decoupled from the Android framework as extensively
as possible, and live somewhere within the `model` or `utils` package. Application layer logic should exist within the
viewmodel as much as possible.

## Requirements
- JDK 17 (or JDK 21)
- Android SDK 34 (`compileSdk`), `minSdk` 23
- Gradle 8.7

## Steps to Follow

1. Submit an issue describing the problem you will be solving with your contribution if one doesn't exist. Assign yourself to the ticket.
2. Fork from `main`.
3. Write your code.
4. Write tests for your code.
5. Run the test suite: `./gradlew test`
6. Ensure your code follows our style guide: `./gradlew ktlint`
7. Submit a pull request to `main`.

## Style Guides

**Commit Messages**
- Use present tense.
- Keep the first line short, but feel free to reference issues in lines after.

**Branch Names**
- Reference the issue number in the branch name.
- Describe the issue solved as briefly as possible.

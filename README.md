![](https://cdn.hashnode.com/res/hashnode/image/upload/v1648736102420/r_3hfiPgN.png)

# **HashPass**

**HashPass** An Android Project built on Android + Hasura

# Application Install

***You can Install QuizZon app from playstore 👇***

[![HashPass](https://img.shields.io/badge/HashPass✅-APK-red.svg?style=for-the-badge&logo=googleplay)](https://play.google.com/store/apps/details?id=com.rohitjakhar.hashpass)

## Setup
Clone the repository on your machine. Open the project on your IDE and connect it to firebase and harperDB and everything will be setup

- Add your firebase json class in app directory
- Add your Hasura Credentital in build.property

## About

In HashPass app you can manage your password easily.
- Fully functionable.
- Clean and Simple Material UI.

### Insights into the app 🔎

![Hashpass.gif](https://cdn.hashnode.com/res/hashnode/image/upload/v1648733632457/dwMKE8lY8.png)

## Built With 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.
 - [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - StateFlow is a state-holder observable flow that emits the current and new state updates to its collectors.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers.
- [Dependency Injection](https://developer.android.com/training/dependency-injection) -
  - [Hilt-Dagger](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
  - [Hilt-ViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack) - DI for injecting `ViewModel`.
- Backend
  - [Firebase](https://firebase.google.com)
    - Firebase Auth - To support email based authentication.
    - Firebase Crashlytics - To report app crashes
  - [Hasura](https://hasura.io/) -  The Hasura GraphQL Engine is an extremely lightweight, high performance product that gives you instant realtime GraphQL APIs on a Postgres database.
- [Apollo](https://www.apollographql.com/) - Apollo Kotlin (formerly Apollo Android) is a GraphQL client that generates Kotlin and Java models from GraphQL queries.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.

# Package Structure

    com.rohit.Quizzon    # Root Package
    .
    ├── data                # For data handling.
    |   ├── local           # Room DB and its related classes
    |   ├── remote          # Firebase, HarperDB and their relative classes
    │   ├── model           # Model data classes, both remote and local entities
    │
    |
    ├── di                  # Dependency Injection
    │   └── module          # DI Modules
    |
    ├── presention           # UI/View layer
    |
    └── utils               # Utility Classes / Kotlin extensions


## Architecture
This app uses [***MVVM (Model View View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

![](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Want to Contribute 🙋‍♂️?

Awesome! If you want to contribute to this project, you're always welcome! See [Contributing Guidelines](CONTRIBUTING.md).

## Want to discuss? 💬

Have any questions, doubts or want to present your opinions, views? You're always welcome. You can [start discussions](https://github.com/RohitJakhar/hashpass/discussions).

## Contact 📩

Have an project? DM us at 👇<br>
[![Mail](https://img.shields.io/badge/Gmail-green.svg?style=for-the-badge&logo=gmail)](mailto://rohitjakhar940@gmail.com)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-red.svg?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/rohitjakhar0/)


## Donation 💰

If this project help you reduce time to develop, you can give me a cup of coffee :)

<a href="https://www.buymeacoffee.com/rohitjakhar" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

<br>

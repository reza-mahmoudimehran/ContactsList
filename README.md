# Contact List Android App

Contact List is an Android application developed in Kotlin that allows users to fetch phone contacts and store them in a local database. It utilizes various modern technologies and follows the MVVM Clean Architecture design pattern. The app also observes changes in contacts and updates the local database accordingly.

## Technologies Used

The Contact List app incorporates the following technologies:

- **MVVM Clean Architecture**: The app follows the Model-View-ViewModel (MVVM) architectural pattern with a Clean Architecture approach, promoting separation of concerns and maintainability.
- **Hilt**: Hilt is used for dependency injection, providing a standardized way to manage and inject dependencies throughout the app.
- **Room**: Room is an Android persistence library that provides an abstraction layer over SQLite database. It is used for storing and retrieving contacts in a local database.
- **Coroutine**: Kotlin Coroutines are utilized for handling asynchronous operations and background tasks in a concise and efficient manner.
- **Flow**: Flow is used for reactive programming, allowing the app to observe changes in the contacts data and react accordingly.
- **Data Binding**: Data Binding library is employed to bind UI components with data sources, reducing boilerplate code and enabling a more declarative UI development approach.
- **Data Store**: Data Store is used for storing key-value pairs securely and asynchronously, providing a modern data storage solution.
- **Navigation Component**: Navigation Component is used for managing navigation within the app, providing a consistent and structured approach to handle navigation between different screens.
- **Espresso**: Espresso is utilized for UI testing, allowing for automated testing of user interactions and UI behavior.
- **Mockito**: Mockito is used for mocking dependencies and facilitating unit testing.

## Installation

1. Clone the repository: `git clone https://github.com/reza-mahmoudimehran/ContactsList.git`
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

## Testing

The Contact List app includes both unit tests and UI tests to ensure its functionality and code quality.

- To run unit tests, right-click on the desired test class or package and select "Run" or use the keyboard shortcut.
- To run UI tests using Espresso, right-click on the desired test class or package and select "Run" or use the keyboard shortcut. Make sure you have an emulator or device connected.

## Contribution

Contributions to the Contact List app are welcome!
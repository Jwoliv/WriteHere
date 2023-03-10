# WriteHere
___
This project is a web forum be themes.

### Technologies
___
:closed_book: Spring (MVC, Data, Security, Boot)<br>
:closed_book: Thymeleaf<br>
:closed_book: PostgreSQL<br>
:closed_book: Mockito / JUnit5

### User capabilities
___
This application has two roles of User:

:key: USER
<br>
:key: ADMIN

#### What a non-registered user can do
* :unlock: Create a post
* :unlock: Add comment for a post
* :unlock: Read a post

#### What can do person with role USER
* :unlock: Everything that was written before
* :unlock: Create complaints about posts and comments
* :unlock: Set like or dislike on the posts or comments
* :unlock: Can to delete posts or comments
* :unlock: Get notifications about active posts or comments of user

#### What can do person with role ADMIN?
* :unlock: Everything that was written before
* :unlock: Access to the admin pages
  * :unlock: Delete all posts
  * :unlock: Delete all comments
  * :unlock: Delete all users who don't have a role: ADMIN

### Usage
___
You need to clone this repository
```
git clone https://github.com/Jwoliv/WriteHere.git
```
Launch the maven
```
mvn clean install
```
Go to this address
```
http://localhost:8020
```
Create account or login in the account use so paths:
* [Login / Sing-in](http://localhost:8020/login)

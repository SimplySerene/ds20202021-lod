# Final Project Data Science 2020/2021

This repository contains the final project. It consists of two parts:
- A Spring Boot Java REST Api
- A React frontend

## Installation

### Requirements

- Nodejs (>= v14)
- Java (v15)
- Maven

### Setup

1. Install backend dependencies using `mvn install`
2. Install frontend dependencies using `cd frontend && npm install`

### Setting the secret keys for the Spotify API

1. Go to https://developer.spotify.com/dashboard/login and login with your spotify account.
2. Create a new application. Here you can find a clientID and a clientSecret.
3. Go to the config file`/src/main/resources/application.properties`.
4. Update the properties `spotifyClientSecret` and `spotifyClientId` with the corresponding values obtained from the spotify application dashboard.

### Build and run

1. Start the backend can be started using `TODO`.
2. Start the frontend using `cd frontend && npm run start`

# Repo to publish App Bundle on the play store

## Setup stand alone tool

- Build jar with `./gradlew app:jar`. Output here: `app/build/libs/play-store.jar`
- Launch `java -jar play-store.jar play-store-publish.json` where the json format is defined here `app/sampledata/`

## Setup for IntelliJ Idea IDE with sampledata

- Remove .template and replace TO_FILL in files inside `app/sampledata/` folder.
- Add your app.aab in `app/sampledata/`
- Run `com.mercandalli.sdk.play_store.main.MainIde`

Based on https://github.com/googlesamples/android-play-publisher-api/tree/master/v3/java
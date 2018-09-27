# Repo to publish App Bundle on the play store

## Setup stand alone tool

- Build jar with `./gradlew app:jar`. Output here: `app/build/libs/play-store.jar`
- Launch `java -jar play-store.jar play-store-publish.json` where the json format is defined here `app/sampledata/`

## Setup for IntelliJ Idea IDE with sampledata

- Remove .template and replace TO_FILL in files inside `app/sampledata/` folder.
- Add your app.aab in `app/sampledata/`
- Run `com.mercandalli.sdk.play_store.main.MainIde`

Based on https://github.com/googlesamples/android-play-publisher-api/tree/master/v3/java

----

# Changelog

## Version 1.00.01 (WorkInProgress)

Feature:
- Upload app bundle to the store.
- Prepare the image upload to the store with the argument `--image` (WorkInProgress)

Image config format is based on this [doc](https://developers.google.com/android-publisher/api-ref/edits/images/upload):

````json
{
  "application_name": "TO_FILL",
  "application_package": "TO_FILL",
  "client_secret_path": "play-store-authentication.json",
  "images": [
    {
      "path": "TO_FILL",
      "language": "TO_FILL",
      "type": "TO_FILL"
    }
  ]
}
````

Where "path":
- Is relative to the json file
- Is absolute
- Is a png file path

Where "type" could be:
- "featureGraphic"
- "icon"
- "phoneScreenshots"
- "promoGraphic"
- "sevenInchScreenshots"
- "tenInchScreenshots"
- "tvBanner"
- "tvScreenshots"
- "wearScreenshots"

Where "language":
- The language code (a BCP-47 language tag) of the localized listing you want to add an image to. 
For example, to select Latin American Spanish, pass "es-419".

----

## Version 1.00.00

Feature:
- Upload app bundle to the store.

Run `java -jar play-store-1-00-00.jar play-store-publish.json --force`

with `play-store-publish.json`:

```json
{
  "application_name": "TO_FILL",
  "application_package": "TO_FILL",
  "channel": "alpha",
  "rollout_percentage": "0.1",
  "app_bundle_path": "app.aab",
  "client_secret_path": "play-store-authentication.json"
}
``` 

and `play-store-authentication.json`:

```json
{
  "installed": {
    "client_id": "TO_FILL",
    "client_secret": "TO_FILL",
    "redirect_uris": [],
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token"
  }
}
``` 

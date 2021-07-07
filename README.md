<p align="center"><br><img src="https://user-images.githubusercontent.com/236501/85893648-1c92e880-b7a8-11ea-926d-95355b8175c7.png" width="128" height="128" /></p>
<h3 align="center">Capacitor Media</h3>
<p align="center"><strong><code>@simplifield/capacitor-media-plugin</code></strong></p>
<p align="center">
  Fork of the https://github.com/capacitor-community/media plugin for enabling extra media capabilities
</p>

<p align="center">
  <a href="https://www.npmjs.com/package/@capacitor-community/media"><img src="https://img.shields.io/npm/l/@capacitor-community/media?style=flat-square" /></a>
<br>
  <!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
<a href="#contributors"><img src="https://img.shields.io/badge/all%20contributors-2-orange?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:END -->

</p>

## Maintainers

| Maintainer   | GitHub                                | Social                                          |
| ------------ | ------------------------------------- | ----------------------------------------------- |
| Stewan Silva | [stewwan](https://github.com/stewwan) | [@StewanSilva](https://twitter.com/StewanSilva) |

## Notice 🚀

We're starting fresh under an official org. If you were using the previous npm package `capacitor-media`, please update your package.json to `@capacitor-community/media`. Check out [changelog](/CHANGELOG.md) for more info.

## Installation

Using npm:

```bash
npm install @simplifield/capacitor-media-plugin
```

Using yarn:

```bash
yarn add @simplifield/capacitor-media-plugin
```

Sync native files:

```bash
npx cap sync
```

## API

- savePhoto
- createAlbum
- getAlbums

## Usage

```js
import { Media } from '@simplifield/capacitor-media-plugin';
const media = new Media();

//
// Save video to a specific album
media
  .savePhoto({ path: '/path/to/the/file', album: 'My Album' })
  .then(console.log)
  .catch(console.log);

//
// Get a list of user albums
media
  .getAlbums()
  .then(console.log) // -> { albums: [{name:'My Album', identifier:'A1-B2-C3-D4'}, {name:'My Another Album', identifier:'E5-F6-G7-H8'}]}
  .catch(console.log);

//
// On Android 10+ files are stored to the external storage. Get Albums will return an emty albums array.
// This is done to prevent crashes because of deprecated DISTINCT keyword

//
// 

import { Media } from '@simplifield/capacitor-media-plugin';
import { Plugins, Capacitor } from '@capacitor/core';

const { Device } = Plugins;
const media = new Media();
const ALBUM_NAME = 'SomeAlbum';

const platform = Capacitor.getPlatform();

if (platform === 'android') {
  return media.savePhoto({ 
    path: filePath,
    album: ALBUM_NAME //is optional on Android.
    // If set and directory is not created - it will be created under the hood.
    // If not set, external storage will be used on Android 10+ (storage/emulated/0/Android/media/yourAppName)
    // or DCIM on Android <= 9
  });
}

// for iOS a special identifier is required, so firstly need to get albums
return media.getAlbums()
  .then(({ albums }) => {
    const mediaAlbum = albums.find((alb) => alb.name === ALBUM_NAME);

    if (!mediaAlbum) {
      return media
        .createAlbum({ name: ALBUM_NAME })
        .then(() => media.getAlbums())
        .then(({ albums }) => {
          const mediaAlbum = albums.find((alb) => alb.name === ALBUM_NAME);

          return mediaAlbum // could be undefined - consider throw
        });
    }
    return mediaAlbum;
  })
  .then((mediaAlbum) =>
    media.savePhoto({
      path: filePath,
      album: this.platformService.isiOS()
        ? mediaAlbum.identifier
        : mediaAlbum.name, // no album - error is thrown))
    })
  );
```

## Disclaimer

Make sure you pass the correct album parameter according to the platform

```js
album: this.platform.is('ios') ? album.identifier : album.name;
```

## iOS setup

- `ionic start my-cap-app --capacitor`
- `cd my-cap-app`
- `npm install —-save @capacitor-community/media`
- `mkdir www && touch www/index.html`
- `npx cap add ios`
- `npx cap open ios`
- sign your app at xcode (general tab)

> Tip: every time you change a native code you may need to clean up the cache (Product > Clean build folder) and then run the app again.

## Android setup

- `ionic start my-cap-app --capacitor`
- `cd my-cap-app`
- `npm install —-save @capacitor-community/media`
- `mkdir www && touch www/index.html`
- `npx cap add android`
- `npx cap open android`
- `[extra step]` in android case we need to tell Capacitor to initialise the plugin:

> on your `MainActivity.java` file add `import com.getcapacitor.community.media.MediaPlugin;` and then inside the init callback `add(MediaPlugin.class);`

Now you should be set to go. Try to run your client using `ionic cap run android --livereload`.

> Tip: every time you change a native code you may need to clean up the cache (Build > Clean Project | Build > Rebuild Project) and then run the app again.

## Example

- https://github.com/capacitor-community/media/blob/master/example

## License

MIT

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://twitter.com/StewanSilva"><img src="https://avatars1.githubusercontent.com/u/719763?v=4" width="75px;" alt=""/><br /><sub><b>Stew</b></sub></a><br /><a href="https://github.com/capacitor-community/media/commits?author=stewwan" title="Code">💻</a> <a href="https://github.com/capacitor-community/media/commits?author=stewwan" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/zakton5"><img src="https://avatars1.githubusercontent.com/u/7013396?v=4" width="75px;" alt=""/><br /><sub><b>Zachary Keeton</b></sub></a><br /><a href="https://github.com/capacitor-community/media/commits?author=zakton5" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

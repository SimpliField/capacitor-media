# @simplifield/capacitor-media-plugin

Capacitor 3 ready plugin inspired by Capacitor-community/media plugin.

Get device albums. Create an album. Save a photo to an album.

## Install

```bash
npm install @simplifield/capacitor-media-plugin
npx cap sync
```

## API

<docgen-index>

* [`getAlbums()`](#getalbums)
* [`savePhoto(...)`](#savephoto)
* [`createAlbum(...)`](#createalbum)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getAlbums()

```typescript
getAlbums() => any
```

**Returns:** <code>any</code>

--------------------


### savePhoto(...)

```typescript
savePhoto(options?: MediaSaveOptions | undefined) => any
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#mediasaveoptions">MediaSaveOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### createAlbum(...)

```typescript
createAlbum(options?: MediaAlbumCreate | undefined) => any
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#mediaalbumcreate">MediaAlbumCreate</a></code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### MediaAlbumResponse

| Prop         | Type            |
| ------------ | --------------- |
| **`albums`** | <code>{}</code> |


#### MediaAlbum

| Prop             | Type                                       |
| ---------------- | ------------------------------------------ |
| **`identifier`** | <code>string</code>                        |
| **`name`**       | <code>string</code>                        |
| **`count`**      | <code>number</code>                        |
| **`type`**       | <code>"smart" \| "shared" \| "user"</code> |


#### MediaSaveOptions

| Prop        | Type                |
| ----------- | ------------------- |
| **`path`**  | <code>string</code> |
| **`album`** | <code>string</code> |


#### MediaAlbumCreate

| Prop       | Type                |
| ---------- | ------------------- |
| **`name`** | <code>string</code> |

</docgen-api>

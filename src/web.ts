import { WebPlugin } from '@capacitor/core';

import type { MediaAlbumResponse, MediaPluginPlugin  } from './definitions';

export class MediaPluginWeb extends WebPlugin implements MediaPluginPlugin {
  getAlbums(): Promise<MediaAlbumResponse> {
    throw Error('Media web is not implemented');
  }

  savePhoto(): Promise<void> {
    throw Error('Media web is not implemented');
  }

  createAlbum(): Promise<void> {
    throw Error('Media web is not implemented');
  }
}

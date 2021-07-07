export interface MediaPluginPlugin {
  getAlbums(): Promise<MediaAlbumResponse>

  savePhoto(options?: MediaSaveOptions): Promise<void>

  createAlbum(options?: MediaAlbumCreate): Promise<void>
}


export interface MediaSaveOptions {
  path: string;
  album?: string;
}

export interface MediaAlbumCreate {
  name: string;
}

export interface MediaAlbum {
  identifier?: string;
  name: string;
  count?: number;
  type?: MediaAlbumType;
}

export interface MediaAlbumResponse {
  albums: MediaAlbum[];
}

export type MediaAlbumType = 'smart' | 'shared' | 'user';
  /**
   * Album is a "smart" album (such as Favorites or Recently Added)
   * Album is a cloud-shared album
   * Album is a user-created album
   */
export interface UrlEntry {
  id: number
  alias: string
  shortUrl: string
  originalUrl: string
  createdAt: string
}

export interface UrlShortenRequest {
  originalUrl: string
  customAlias?: string
}

export interface UrlShortenResponse {
  alias: string
  shortUrl: string
  originalUrl: string
}

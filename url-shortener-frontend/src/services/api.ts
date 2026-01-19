import axios from 'axios'
import type {
  UrlShortenRequest,
  UrlShortenResponse,
  UrlEntry,
} from '../types/url'

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const getAllShortenUrls = () =>
  api.get<UrlEntry[]>('/urls').then(res => res.data)

export const shortenUrl = (request: UrlShortenRequest) =>
  api.post<UrlShortenResponse>('/shorten', request).then(res => res.data)

export const deleteAliasUrl = (alias: string) => api.delete(`/${alias}`)

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteAliasUrl, getAllShortenUrls, shortenUrl } from '../services/api'
import type { UrlShortenRequest } from '../types/url'

export const useUrls = () => {
  return useQuery({
    queryKey: ['urls'],
    queryFn: getAllShortenUrls,
  })
}

export const useShortenUrl = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: UrlShortenRequest) => shortenUrl(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['urls'] })
    },
  })
}

export const useDeleteUrl = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (alias: string) => deleteAliasUrl(alias),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['urls'] })
    },
  })
}

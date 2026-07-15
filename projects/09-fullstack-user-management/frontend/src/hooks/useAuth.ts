import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { authApi } from '../api/auth'

export function useMe() {
  return useQuery({
    queryKey: ['me'],
    queryFn: authApi.me,
    retry: false,
    staleTime: 5 * 60_000,
  })
}

export function useLogin() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.login,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['me'] }),
  })
}

export function useLogout() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => qc.setQueryData(['me'], null),
  })
}

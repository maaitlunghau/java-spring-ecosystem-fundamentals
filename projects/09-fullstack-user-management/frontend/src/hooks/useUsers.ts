import { useQuery } from '@tanstack/react-query'
import { usersApi, type UsersParams } from '../api/users'

export function useUsers(params: UsersParams) {
  return useQuery({
    queryKey: ['users', params],
    queryFn: () => usersApi.list(params),
    placeholderData: (prev) => prev,
  })
}

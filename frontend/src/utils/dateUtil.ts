export function formatDateStr(dateTimeStr: string | null) {
  if (!dateTimeStr) return 'xx/xx/xxxx'

  const date = new Date(dateTimeStr)
  return new Intl.DateTimeFormat('en-US', {
    month: '2-digit',
    day: '2-digit',
    year: 'numeric',
  })
    .format(date)
    .toString()
}

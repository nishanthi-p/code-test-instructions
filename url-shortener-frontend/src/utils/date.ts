export const formatDateToLocale = (
  date: string | number | Date,
  options?: Intl.DateTimeFormatOptions
) =>
  new Intl.DateTimeFormat(
    undefined,
    options ?? {
      dateStyle: 'medium',
      timeStyle: 'short',
    }
  ).format(new Date(date))

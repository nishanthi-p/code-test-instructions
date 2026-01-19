export const URL_REGEX =
  /^(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/[^\s]*)?$/

export const validateOriginalUrl = (value: string): true | string => {
  if (value.length > 2048) {
    return 'URL must be 2048 characters or less'
  }

  if (!URL_REGEX.test(value)) {
    return 'Please enter a valid URL'
  }

  try {
    new URL(value.startsWith('http') ? value : `http://${value}`)
    return true
  } catch {
    return 'Please enter a valid URL'
  }
}

export const validateCustomAlias = (alias?: string): true | string => {
  if (!alias) return true

  if (alias.length > 50) {
    return 'Custom alias must be 50 characters or less'
  }

  return /^[a-zA-Z0-9_-]+$/.test(alias)
    ? true
    : 'Only letters, numbers, hyphens, and underscores are allowed'
}

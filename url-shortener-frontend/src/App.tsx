import 'bootstrap/dist/css/bootstrap.min.css'
import { useUrls, useShortenUrl, useDeleteUrl } from './hooks/useShortenUrl'
import { useForm } from 'react-hook-form'
import { validateCustomAlias, validateOriginalUrl } from './utils/validators'
import { formatDateToLocale } from './utils/date'

type FormValues = {
  originalUrl: string
  customAlias?: string
}

function App() {
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset,
  } = useForm<FormValues>({
    mode: 'onChange',
  })

  const { data: urls = [], isLoading, error } = useUrls()
  const shortenUrlMutation = useShortenUrl()
  const deleteAliasMutation = useDeleteUrl()

  const onSubmit = (data: FormValues) => {
    shortenUrlMutation.mutate(
      {
        originalUrl: data.originalUrl.trim(),
        customAlias: data.customAlias?.trim() || undefined,
      },
      {
        onSuccess: () => reset(),
      }
    )
  }

  const handleDeleteAlias = (alias: string) => {
    deleteAliasMutation.mutate(alias)
  }

  return (
    <div className="min-vh-100 bg-light py-4 py-md-5">
      <div className="container-fluid px-3 px-md-5">
        <div className="row">
          <div className="col-12">
            <h1 className="text-center mb-4">URL Shortener</h1>

            {(error ||
              shortenUrlMutation.error ||
              deleteAliasMutation.error) && (
              <div className="alert alert-danger" role="alert">
                {(shortenUrlMutation.error as any)?.response?.data?.message ||
                  error?.message ||
                  'An error occurred'}
              </div>
            )}

            {/* Create Short URL */}
            <div className="card shadow-sm mb-4">
              <div className="card-body p-3 p-md-4">
                <h5 className="card-title mb-3">Create Short URL</h5>
                <form onSubmit={handleSubmit(onSubmit)}>
                  <div className="mb-3">
                    <label className="form-label">Original URL</label>
                    <input
                      type="text"
                      className={`form-control ${errors.originalUrl ? 'is-invalid' : ''}`}
                      placeholder="example.com or https://example.com"
                      {...register('originalUrl', {
                        required: 'URL is required',
                        validate: validateOriginalUrl,
                      })}
                    />

                    {errors.originalUrl && (
                      <div className="invalid-feedback">
                        {errors.originalUrl.message}
                      </div>
                    )}
                  </div>
                  <div className="mb-3">
                    <label className="form-label">
                      Custom Alias{' '}
                      <span className="text-muted">(optional)</span>
                    </label>

                    <div className="position-relative">
                      <div className="input-group flex-nowrap">
                        <span className="input-group-text d-none d-sm-inline">
                          http://localhost:8080/
                        </span>
                        <span className="input-group-text d-sm-none">/</span>

                        <input
                          type="text"
                          className={`form-control ${errors.customAlias ? 'is-invalid' : ''}`}
                          placeholder="my-alias"
                          {...register('customAlias', {
                            validate: value =>
                              validateCustomAlias(value) ||
                              'Only letters, numbers, hyphens, and underscores are allowed',
                          })}
                        />
                      </div>

                      {errors.customAlias && (
                        <div className="invalid-feedback d-block mt-1">
                          {errors.customAlias.message}
                        </div>
                      )}
                    </div>

                    <div className="form-text">
                      Leave empty for auto-generated alias.
                    </div>
                  </div>
                  <button
                    type="submit"
                    className="btn btn-primary w-100 w-sm-auto"
                    disabled={!isValid || shortenUrlMutation.isPending}
                  >
                    {shortenUrlMutation.isPending
                      ? 'Shortening...'
                      : 'Shorten URL'}
                  </button>
                </form>
              </div>
            </div>

            {/* List of Shortened URLs */}
            <div className="card shadow-sm">
              <div className="card-body p-3 p-md-4">
                <h5 className="card-title mb-3">Shortened URLs</h5>
                {isLoading ? (
                  <div className="text-center py-4">
                    <div className="spinner-border" role="status">
                      <span className="visually-hidden">Loading...</span>
                    </div>
                  </div>
                ) : urls.length === 0 ? (
                  <p className="text-center text-muted py-4 mb-0">
                    No URLs shortened yet
                  </p>
                ) : (
                  <>
                    {/* Desktop Table */}
                    <div className="d-none d-md-block table-responsive">
                      <table className="table table-striped table-hover mb-0">
                        <thead className="table-dark">
                          <tr>
                            <th>Alias</th>
                            <th>Short URL</th>
                            <th>Original URL</th>
                            <th>Created</th>
                            <th>Actions</th>
                          </tr>
                        </thead>
                        <tbody>
                          {urls.map(url => (
                            <tr key={url.id}>
                              <td>
                                <code>{url.alias}</code>
                              </td>
                              <td>
                                <a
                                  href={url.shortUrl}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  className="text-break"
                                >
                                  {url.shortUrl}
                                </a>
                              </td>
                              <td
                                className="text-truncate"
                                style={{ maxWidth: '200px' }}
                                title={url.originalUrl}
                              >
                                {url.originalUrl}
                              </td>
                              <td className="text-nowrap">
                                {formatDateToLocale(url.createdAt)}
                              </td>
                              <td>
                                <div className="btn-group btn-group-sm">
                                  <button
                                    className="btn btn-outline-danger"
                                    onClick={() => handleDeleteAlias(url.alias)}
                                    disabled={deleteAliasMutation.isPending}
                                  >
                                    Delete
                                  </button>
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>

                    {/* Mobile Table */}
                    <div className="d-md-none">
                      {urls.map(url => (
                        <div key={url.id} className="card mb-3 border">
                          <div className="card-body p-3">
                            <div className="d-flex justify-content-between align-items-start mb-2">
                              <code className="fs-6">{url.alias}</code>
                              <small className="text-muted">
                                {formatDateToLocale(url.createdAt)}
                              </small>
                            </div>
                            <div className="mb-2">
                              <small className="text-muted d-block">
                                Short URL:
                              </small>
                              <a
                                href={url.shortUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-break small"
                              >
                                {url.shortUrl}
                              </a>
                            </div>
                            <div className="mb-3">
                              <small className="text-muted d-block">
                                Original URL:
                              </small>
                              <span className="text-break small">
                                {url.originalUrl}
                              </span>
                            </div>
                            <div className="d-flex gap-2">
                              <button
                                className="btn btn-sm btn-outline-danger flex-grow-1"
                                onClick={() => handleDeleteAlias(url.alias)}
                                disabled={deleteAliasMutation.isPending}
                              >
                                Delete
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App

import { Link } from 'react-router'

export function NotFoundPage() {
  return (
    <div className="mx-auto max-w-5xl px-6 py-24 text-center">
      <h1 className="text-3xl font-bold">Nie znaleziono strony</h1>
      <Link to="/" className="mt-6 inline-block text-brand hover:underline">
        Wróć na stronę główną
      </Link>
    </div>
  )
}

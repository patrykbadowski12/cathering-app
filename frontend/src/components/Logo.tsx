import { Leaf } from 'lucide-react'
import { Link } from 'react-router'

export function Logo() {
  return (
    <Link to="/" className="group inline-flex items-center gap-2 text-xl font-bold tracking-tight">
      <span className="inline-flex size-8 items-center justify-center rounded-lg bg-brand text-brand-foreground transition group-hover:rotate-6">
        <Leaf aria-hidden className="size-5" />
      </span>
      <span>Catering</span>
    </Link>
  )
}

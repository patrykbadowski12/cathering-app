import { createBrowserRouter } from 'react-router'
import { HomePage } from '@/shop/HomePage'
import { NotFoundPage } from '@/shop/NotFoundPage'
import { ShopLayout } from '@/shop/ShopLayout'

export const router = createBrowserRouter([
  {
    path: '/',
    Component: ShopLayout,
    children: [
      { index: true, Component: HomePage },
      { path: '*', Component: NotFoundPage },
    ],
  },
])

import { createBrowserRouter, Navigate } from 'react-router'
import { HomePage } from '@/shop/HomePage'
import { NotFoundPage } from '@/shop/NotFoundPage'
import { ShopLayout } from '@/shop/ShopLayout'

export const router = createBrowserRouter([
  {
    path: '/admin',
    lazy: async () => {
      const { AdminLayout } = await import('@/admin/AdminLayout')
      return { Component: AdminLayout }
    },
    children: [
      { index: true, element: <Navigate to="diety" replace /> },
      {
        path: 'diety',
        lazy: async () => {
          const { DietPlansAdminPage } = await import('@/admin/DietPlansAdminPage')
          return { Component: DietPlansAdminPage }
        },
      },
      { path: '*', element: <Navigate to="diety" replace /> },
    ],
  },
  {
    path: '/',
    Component: ShopLayout,
    children: [
      { index: true, Component: HomePage },
      { path: '*', Component: NotFoundPage },
    ],
  },
])

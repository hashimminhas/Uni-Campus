import { createRouter, createWebHistory } from 'vue-router'
import LandingView from '../views/LandingView.vue'
import HomeView from '../views/HomeView.vue'
import LibraryView from '../views/LibraryView.vue'
import AdminView from '../views/AdminView.vue'
import StudentsView from '../views/StudentsView.vue'

const routes = [
  { path: '/',         name: 'home',          component: LandingView },
  { path: '/students', name: 'students',       component: StudentsView },
  { path: '/courses',  name: 'courses',        component: HomeView },
  { path: '/library',  name: 'library',        component: LibraryView },
  { path: '/admin',    name: 'admin',          component: AdminView },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router

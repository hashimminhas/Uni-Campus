import { createRouter, createWebHistory } from 'vue-router'
import LandingView from '../views/LandingView.vue'
import HomeView from '../views/HomeView.vue'
import LibraryView from '../views/LibraryView.vue'
import AdminView from '../views/AdminView.vue'
import DormitoryView from '../views/DormitoryView.vue'
import StudentsView from '../views/StudentsView.vue'
import BillingView from '../views/BillingView.vue'

const routes = [
  { path: '/',         name: 'home',          component: LandingView },
  { path: '/students', name: 'students',       component: StudentsView },
  { path: '/courses',  name: 'courses',        component: HomeView },
  { path: '/library',  name: 'library',        component: LibraryView },
  { path: '/billing',  name: 'billing',        component: BillingView },
  { path: '/dormitory',name: 'dormitory',      component: DormitoryView },
  { path: '/meal-plan',name: 'meal-plan',      component: () => import('../views/MealPlanView.vue') },
  { path: '/admin',    name: 'admin',          component: AdminView },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router

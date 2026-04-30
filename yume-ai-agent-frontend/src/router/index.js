import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LoveMaster from '../views/LoveMaster.vue'
import ManusAgent from '../views/ManusAgent.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/love', component: LoveMaster },
  { path: '/manus', component: ManusAgent }
]

export default createRouter({
  history: createWebHistory(),
  routes
})

'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import styles from './dashboard.module.css'

export default function DashboardPage() {
  const router = useRouter()
  const [user, setUser] = useState<string>('')

  useEffect(() => {
    const auth = localStorage.getItem('auth')
    if (!auth) {
      router.push('/login')
      return
    }

    // Fetch user profile
    fetch('http://localhost:8080/api/profile', {
      headers: {
        'Authorization': `Basic ${auth}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error('Unauthorized')
        return res.text()
      })
      .then((data) => setUser(data))
      .catch(() => {
        localStorage.removeItem('auth')
        router.push('/login')
      })
  }, [router])

  const handleLogout = () => {
    localStorage.removeItem('auth')
    router.push('/login')
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.welcome}>{user || 'Loading...'}</p>
        <button onClick={handleLogout} className={styles.button}>
          Logout
        </button>
      </div>
    </div>
  )
}
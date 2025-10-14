"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import styles from "./dashboard.module.css";
import { getProfile, logout } from "@/services/AuthenticationService";
import type { UserProfile } from "@/gql/graphql";

export default function DashboardPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getProfile()
      .then((userProfile) => {
        setProfile(userProfile);
        setLoading(false);
      })
      .catch(() => {
        localStorage.removeItem("username");
        router.push("/login");
      });
  }, [router]);

  const handleLogout = () => {
    logout()
      .then(() => {
        localStorage.removeItem("username");
        router.push("/login");
      })
      .catch((errorMsg) => {
        console.error("Logout error:", errorMsg);
        // Even if logout fails, clear local data and redirect
        localStorage.removeItem("username");
        router.push("/login");
      });
  };

  if (loading) {
    return (
      <div className={styles.container}>
        <div className={styles.card}>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.welcome}>
          Welcome, {profile?.firstName} {profile?.lastName}!
        </p>
        <p style={{ color: "#718096", marginBottom: "24px" }}>
          Username: {profile?.username} | Role: {profile?.role}
        </p>
        <button onClick={handleLogout} className={styles.button}>
          Logout
        </button>
      </div>
    </div>
  );
}

"use client";

import { useCallback, useState } from "react";
import Link from "next/link";
import {
  Box,
  Button,
  Card,
  CardContent,
  IconButton,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { Credentials, login } from "@/services/UserService";

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [credentials, setCredentials] = useState<Credentials>({
    username: "",
    password: "",
  });

  const handleLogin = useCallback(() => {
    login(credentials)
      .then((response) => {
        if (response.status === 401) {
          // User unauthorized
        }
      })
      .catch((error) => {
        // TODO
        debugger;
      });
  }, [credentials]);

  const handleInputChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setCredentials((currentCredentials) => {
        if (event.target.name === "username") {
          return {
            ...currentCredentials,
            username: event.target.value,
          };
        } else if (event.target.name === "password") {
          return {
            ...currentCredentials,
            password: event.target.value,
          };
        } else {
          return currentCredentials;
        }
      });
    },
    [],
  );

  return (
    <Box sx={{ height: "100vh", width: "100vw", alignContent: "center" }}>
      <Card sx={{ margin: "auto", width: "50vw" }}>
        <CardContent
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Typography variant="h4">Welcome Back</Typography>
          <Typography variant="body1" sx={{ marginBottom: "20px" }}>
            Please login to your account
          </Typography>
          <TextField
            sx={{ marginBottom: "20px", width: "75%" }}
            label="Username"
            name="username"
            placeholder="Enter your username"
            onChange={handleInputChange}
          />
          <TextField
            sx={{ marginBottom: "20px", width: "75%" }}
            type={showPassword ? "text" : "password"}
            label="Password"
            name="password"
            placeholder="Enter your password"
            onChange={handleInputChange}
            slotProps={{
              input: {
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={() =>
                        setShowPassword((prevShowPassword) => !prevShowPassword)
                      }
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              },
            }}
          />
          <Button
            sx={{ marginBottom: "20px", width: "50%" }}
            variant="contained"
            onClick={handleLogin}
          >
            Login
          </Button>
          <Box>
            <Typography variant="body1">
              {"Don't have an account?  "}
              <Link href="/register">Register here</Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}

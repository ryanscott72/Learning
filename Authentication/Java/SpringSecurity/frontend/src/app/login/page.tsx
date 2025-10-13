"use client";

import { useCallback, useState } from "react";
import { useRouter } from "next/navigation";

import Link from "next/link";

import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { LoginInput } from "@/gql/graphql";
import { login } from "@/services/AuthenticationService";

export default function LoginPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>();
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [credentials, setCredentials] = useState<LoginInput>({
    username: "",
    password: "",
  });

  const handleLogin = useCallback(
    (event: React.FormEvent<HTMLFormElement>) => {
      setErrorMessage(null);
      event.preventDefault();
      setIsLoading(true);
      login(credentials)
        .then((username) => router.push("/dashboard"))
        .catch(setErrorMessage)
        .finally(() => setIsLoading(false));
    },
    [router, credentials],
  );

  const handleInputChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setCredentials((currentCredentials) => {
        return {
          ...currentCredentials,
          [event.target.name]: event.target.value,
        };
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
          {errorMessage !== null && errorMessage !== undefined && (
            <Alert sx={{ marginBottom: "16px" }} severity="error">
              {errorMessage}
            </Alert>
          )}
          <form
            style={{
              display: "flex",
              flexDirection: "column",
              width: "75%",
              alignItems: "center",
            }}
            onSubmit={handleLogin}
          >
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              label="Username"
              name="username"
              placeholder="Enter your username"
              onChange={handleInputChange}
              value={credentials.username}
            />
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              type={showPassword ? "text" : "password"}
              label="Password"
              name="password"
              placeholder="Enter your password"
              onChange={handleInputChange}
              value={credentials.password}
              slotProps={{
                input: {
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={() =>
                          setShowPassword(
                            (prevShowPassword) => !prevShowPassword,
                          )
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
              loading={isLoading}
              disabled={
                credentials.username === "" || credentials.password === ""
              }
              type="submit"
            >
              Login
            </Button>
          </form>
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

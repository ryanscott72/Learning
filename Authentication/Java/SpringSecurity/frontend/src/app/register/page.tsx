"use client";

import { useCallback, useState } from "react";
import { useRouter } from "next/navigation";

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
import { RegisterInput } from "@/gql/graphql";
import { register } from "@/services/AuthenticationService";

export default function RegisterPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>();
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [showConfirmPassword, setShowConfirmPassword] =
    useState<boolean>(false);
  const [registration, setRegistration] = useState<RegisterInput>({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [passwordsDontMatch, setPasswordsDontMatch] = useState<boolean>(false);

  const handleRegister = useCallback(
    (event: React.FormEvent<HTMLFormElement>) => {
      setErrorMessage(null);
      event.preventDefault();
      setIsLoading(true);
      register(registration)
        .then(() => router.push("/login"))
        .catch(setErrorMessage)
        .finally(() => setIsLoading(false));
    },
    [router, registration],
  );

  const handleInputChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setRegistration((currentRegistration) => {
        return {
          ...currentRegistration,
          [event.target.name]: event.target.value,
        };
      });
    },
    [],
  );

  const handleCheckPasswords = useCallback(
    (event: React.FocusEvent<HTMLInputElement>) => {
      if (event.target.value !== registration.password) {
        setPasswordsDontMatch(true);
      } else {
        setPasswordsDontMatch(false);
      }
    },
    [registration.password],
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
          <Typography variant="h4">Create Account</Typography>
          <Typography variant="body1">Join us today</Typography>
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
            onSubmit={handleRegister}
          >
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              label="Username"
              name="username"
              placeholder="Enter your username"
              onChange={handleInputChange}
              value={registration.username}
            />
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              type={showPassword ? "text" : "password"}
              label="Password"
              name="password"
              placeholder="Enter your password"
              onChange={handleInputChange}
              value={registration.password}
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
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              type={showConfirmPassword ? "text" : "password"}
              label="Confirm Password"
              name="confirmPassword"
              placeholder="Confirm your password"
              onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                setConfirmPassword(e.target.value)
              }
              value={confirmPassword}
              onBlur={handleCheckPasswords}
              error={passwordsDontMatch}
              helperText={
                passwordsDontMatch && "The passwords you entered do not match."
              }
              slotProps={{
                input: {
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={() =>
                          setShowConfirmPassword(
                            (prevShowConfirmPassword) =>
                              !prevShowConfirmPassword,
                          )
                        }
                        edge="end"
                      >
                        {showConfirmPassword ? (
                          <VisibilityOff />
                        ) : (
                          <Visibility />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                },
              }}
            />
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              label="First Name"
              name="firstName"
              placeholder="Enter your first name"
              onChange={handleInputChange}
              value={registration.firstName}
            />
            <TextField
              required
              sx={{ marginBottom: "20px", width: "75%" }}
              label="Last Name"
              name="lastName"
              placeholder="Enter your last name"
              onChange={handleInputChange}
              value={registration.lastName}
            />
            <Button
              sx={{ marginBottom: "20px", width: "50%" }}
              variant="contained"
              loading={isLoading}
              disabled={
                registration.username === "" ||
                registration.password === "" ||
                registration.firstName === "" ||
                registration.lastName === "" ||
                registration.password !== confirmPassword
              }
              type="submit"
            >
              Login
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}

type Credentials = {
  username: string;
  password: string;
};

function login(credentials: Credentials): Promise<Response> {
  const encryptedCredentials = btoa(
    `${credentials.username}:${credentials.password}`,
  );

  return fetch("http://localhost:8080/api/profile", {
    method: "GET",
    headers: {
      Authorization: `Basic ${encryptedCredentials}`,
    },
  });
}

function registerUser() {}

export type { Credentials };
export { login, registerUser };

import { graphql } from "@/gql";
import {
  LoginInput,
  Mutation,
  Query,
  RegisterInput,
  UserProfile,
} from "@/gql/graphql";
import { graphqlClient } from "@/lib/graphql-client";

const LOGIN_MUTATION = graphql(`
  mutation Login($input: LoginInput!) {
    login(input: $input) {
      success
      message
      username
    }
  }
`);

const REGISTER_MUTATION = graphql(`
  mutation Register($input: RegisterInput!) {
    register(input: $input) {
      success
      message
      userId
    }
  }
`);

const PROFILE_QUERY = graphql(`
  query Profile {
    profile {
      username
      firstName
      lastName
      role
    }
  }
`);

const LOGOUT_MUTATION = graphql(`
  mutation Logout {
    logout
  }
`);

export async function login(input: LoginInput): Promise<string> {
  try {
    const data = await graphqlClient.request<Pick<Mutation, "login">>(
      LOGIN_MUTATION,
      { input },
    );

    if (data.login.success) {
      // Store username and return it
      return Promise.resolve(data.login.username || "");
    } else {
      return Promise.reject(data.login.message);
    }
  } catch (err: any) {
    const errorMsg =
      err.response?.errors?.[0]?.message || "Failed to connect to server";
    return Promise.reject(errorMsg);
  }
}

export async function register(input: RegisterInput): Promise<void> {
  try {
    const data = await graphqlClient.request<Pick<Mutation, "register">>(
      REGISTER_MUTATION,
      { input },
    );

    if (data.register.success) {
      return Promise.resolve();
    } else {
      return Promise.reject(data.register.message);
    }
  } catch (err: any) {
    const errorMsg =
      err.response?.errors?.[0]?.message || "Failed to connect to server";
    return Promise.reject(errorMsg);
  }
}

export async function getProfile(): Promise<UserProfile> {
  try {
    const data =
      await graphqlClient.request<Pick<Query, "profile">>(PROFILE_QUERY);

    if (data.profile) {
      return Promise.resolve(data.profile);
    } else {
      return Promise.reject("Profile not found");
    }
  } catch (err: any) {
    const errorMsg =
      err.response?.errors?.[0]?.message || "Failed to fetch profile";
    return Promise.reject(errorMsg);
  }
}

export async function logout(): Promise<void> {
  try {
    await graphqlClient.request<Pick<Mutation, "logout">>(LOGOUT_MUTATION);
    return Promise.resolve();
  } catch (err: any) {
    const errorMsg = err.response?.errors?.[0]?.message || "Failed to logout";
    return Promise.reject(errorMsg);
  }
}

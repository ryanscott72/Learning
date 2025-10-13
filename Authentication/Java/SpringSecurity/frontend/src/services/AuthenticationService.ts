import { graphql } from "@/gql";
import {
  LoginInput,
  LoginResponse,
  Mutation,
  Query,
  RegisterInput,
  RegisterResponse,
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
    const data = await graphqlClient.request<LoginResponse>(LOGIN_MUTATION, {
      input,
    });

    if (data.success) {
      // Store username and return it
      return Promise.resolve(data.username || "");
    } else {
      return Promise.reject(data.message);
    }
  } catch (err: unknown) {
    return Promise.reject(getErrorMessageFromUnknownError(err));
  }
}

export async function register(
  input: RegisterInput,
): Promise<RegisterResponse | string> {
  try {
    const data = await graphqlClient.request<RegisterResponse>(
      REGISTER_MUTATION,
      { input },
    );

    if (data.success) {
      return Promise.resolve(data);
    } else {
      return Promise.reject(data.message);
    }
  } catch (err: unknown) {
    return Promise.reject(getErrorMessageFromUnknownError(err));
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
  } catch (err: unknown) {
    return Promise.reject(getErrorMessageFromUnknownError(err));
  }
}

export async function logout(): Promise<void | string> {
  try {
    await graphqlClient.request<Pick<Mutation, "logout">>(LOGOUT_MUTATION);
    return Promise.resolve();
  } catch (err: unknown) {
    return getErrorMessageFromUnknownError(err);
  }
}

function getErrorMessageFromUnknownError(err: unknown): string {
  if (err instanceof TypeError) {
    return "Failed to connect to server";
  }
  return "TODO";
}

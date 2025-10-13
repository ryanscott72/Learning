import { graphql } from "@/gql";
import {
  LoginInput,
  LoginMutation,
  Mutation,
  Query,
  RegisterInput,
  RegisterMutation,
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
    const data = await graphqlClient.request<LoginMutation>(LOGIN_MUTATION, {
      input,
    });

    if (data.login.success && data.login.username) {
      // Store username and return it
      return data.login.username;
    } else {
      throw new Error(data.login.message);
    }
  } catch (err: unknown) {
    throw new Error(getErrorMessageFromUnknownError(err));
  }
}

export async function register(input: RegisterInput): Promise<void | string> {
  try {
    const data = await graphqlClient.request<RegisterMutation>(
      REGISTER_MUTATION,
      { input },
    );

    if (data.register.success) {
      return;
    } else {
      throw new Error(data.register.message);
    }
  } catch (err: unknown) {
    throw new Error(getErrorMessageFromUnknownError(err));
  }
}

export async function getProfile(): Promise<UserProfile> {
  try {
    const data =
      await graphqlClient.request<Pick<Query, "profile">>(PROFILE_QUERY);

    if (data.profile) {
      return data.profile;
    } else {
      throw new Error("Profile not found");
    }
  } catch (err: unknown) {
    throw new Error(getErrorMessageFromUnknownError(err));
  }
}

export async function logout(): Promise<void | string> {
  try {
    await graphqlClient.request<Pick<Mutation, "logout">>(LOGOUT_MUTATION);
    return;
  } catch (err: unknown) {
    throw new Error(getErrorMessageFromUnknownError(err));
  }
}

function getErrorMessageFromUnknownError(err: unknown): string {
  if (err instanceof TypeError) {
    return "Failed to connect to server";
  }
  return "TODO";
}

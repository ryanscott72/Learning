import { GraphQLClient } from "graphql-request";

export const graphqlClient = new GraphQLClient(
  "http://localhost:8080/graphql",
  {
    credentials: "include",
  },
);

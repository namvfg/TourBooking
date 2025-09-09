import axios from "axios";

const SERVER_CONTEXT = "appv1";
const BASE_URL = `http://localhost:8080/${SERVER_CONTEXT}/api`

export const endpoints = {
    "login": "/login",

    //user
    "register": "/register",
    "profile": "/secure/profile",

    //provider
    "provider-register": "/provider/register",
    "provider-profile": "/secure/provider/profile"
};

export const authApis = (token) => axios.create({
  baseURL: BASE_URL,
  headers: {
    Authorization: `Bearer ${token}`,
  },
});

export default axios.create({
  baseURL: BASE_URL,
});
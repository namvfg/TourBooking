import axios from "axios";

const SERVER_CONTEXT = "appv1";
const BASE_URL = `http://localhost:8080/${SERVER_CONTEXT}/api`

export const endpoints = {
    "login": "/login",
    "service-post-list": "/service-post/all",
    "service-post-detail": "/service-post", 

    //user
    "register": "/register",
    "profile": "/secure/profile",

    //provider
    "provider-register": "/provider/register",
    "provider-profile": "/secure/provider/profile",
    "provider-request-permission": "/secure/provider/request-permission",
    "provider-check-status": "/secure/provider/check-status",
    "service-post-add": "/secure/service-post/add",
    "service-post-edit": "/secure/service-post/edit",    
    "service-post-delete": "/secure/service-post/delete",
    "service-post-delete": "/secure/service-post/edit",

    //enums
    "service-types": "/enums/service-types"
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
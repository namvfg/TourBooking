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
    "payment": (postId) => `/secure/transaction/service-post/${postId}`,

    //provider
    "provider-register": "/provider/register",
    "provider-profile": "/secure/provider/profile",
    "provider-request-permission": "/secure/provider/request-permission",
    "provider-service-permissions": "/secure/provider/service-permissions",
    "provider-check-status": "/secure/provider/check-status",
    "service-post-add": "/secure/service-post/add",
    "service-post-edit": "/secure/service-post/edit",    
    "service-post-delete": "/secure/service-post/delete",


    //enums
    "service-types": "/enums/service-types",
    "payment-methods": "/enums/transaction-types",

    //upload image cho ckeditor
    "upload-image": "/secure/upload-image"
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
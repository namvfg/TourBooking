import axios from "axios";

const SERVER_CONTEXT = "appv1";
const BASE_URL = `http://localhost:8080/${SERVER_CONTEXT}/api`

export const endpoints = {
  "login": "/login",
  "service-post-list": "/service-post/all",
  "service-post-detail": "/service-post",


  "register": "/register",
  "profile": "/secure/profile",
  "user-update-profile": "/secure/users/update-profile",
  "user-change-password": "/secure/users/change-password",
  "payment": (postId) => `/secure/transaction/service-post/${postId}`,
  "service-post-search": "/service-post/search",
  "user-transactions": "/secure/transaction/user",


  "provider-register": "/provider/register",
  "provider-profile": "/secure/provider/profile",
  "provider-update-profile": "/secure/provider/update-profile",
  "provider-request-permission": "/secure/provider/request-permission",
  "provider-service-permissions": "/secure/provider/service-permissions",
  "provider-check-status": "/secure/provider/check-status",
  "provider-by-id": "/provider",
  "provider-ratings": "/secure/provider",
  "provider-add-rating": (id) => `/secure/provider/${id}/rating`,
  "provider-transactions": (postId) => `/secure/transaction/service-post/${postId}`,


  "service-post-add": "/secure/service-post/add",
  "service-post-edit": "/secure/service-post/edit",
  "service-post-delete": "/secure/service-post/delete",


  "service-types": "/enums/service-types",
  "payment-methods": "/enums/transaction-types",


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
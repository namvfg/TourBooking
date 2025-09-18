import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import logo from './logo.svg';
import cookies from 'react-cookies';
import { Container } from 'react-bootstrap';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import { MyUserContext } from './components/configs/Context';
import { useReducer, useState } from 'react';
import MyUserReducer from './components/reducers/MyUserReducer';

import Home from './components/Home';
import Login from './components/user/Login';
import Register from './components/user/Register';
import Profile from './components/user/Profile';
import Unauthorized from './components/others/Unauthorized';
import ProtectedRoute from './components/layout/ProtectedRoute';
import ServicePostList from "./components/ServicePostList";
import ServicePostDetail from './components/provider/ServicePostDetail';
import AddServicePost from "./components/provider/AddServicePost";
import Payment from './components/user/Payment';
import PaymentReturn from './components/user/PaymentReturn';
import ProviderDetail from './components/provider/ProviderDetail';
import UpdateProfile from './components/user/UpdateProfile';
import ProviderHome from './components/provider/ProviderHome';
import AuthRedirector from './components/layout/AuthRedirector';
import UserTransaction from './components/user/UserTransaction';
import CompareService from './components/user/ComparePage';
import ComparePage from './components/user/ComparePage';

const App = () => {
  const [user, dispatch] = useReducer(MyUserReducer, undefined);

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <BrowserRouter>
        <AuthRedirector onLoaded={() => { }} />
        <div style={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
          <Header />

          <main style={{ flex: 1 }}>
            <Container className="py-4">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/unauthorized" element={<Unauthorized />} />
                <Route path="/service-posts" element={<ServicePostList />} />
                <Route path="/service-post/:id" element={<ServicePostDetail />} />

                <Route
                  path="/compare/:leftId"
                  element={
                    <ProtectedRoute allowedRoles={["USER"]} user={user}>
                      <ComparePage  />
                    </ProtectedRoute>
                  }
                />

                <Route
                  path="/user-transactions"
                  element={
                    <ProtectedRoute allowedRoles={["USER"]} user={user}>
                      <UserTransaction />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/add-service-post"
                  element={
                    <ProtectedRoute allowedRoles={["PROVIDER"]} user={user}>
                      <AddServicePost />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/payment"
                  element={
                    <ProtectedRoute allowedRoles={["USER"]} user={user}>
                      <Payment />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/provider-home"
                  element={
                    <ProtectedRoute allowedRoles={["PROVIDER"]} user={user}>
                      <ProviderHome />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/update-profile"
                  element={
                    <ProtectedRoute allowedRoles={["USER", "PROVIDER"]} user={user}>
                      <UpdateProfile />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/payment-return"
                  element={
                    <ProtectedRoute allowedRoles={["USER"]} user={user}>
                      <PaymentReturn />
                    </ProtectedRoute>
                  }
                />
                <Route path="/provider/:providerId" element={<ProviderDetail />} />
                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute allowedRoles={["USER", "PROVIDER", "ADMIN"]} user={user}>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
              </Routes>
            </Container>
          </main>

          <Footer />
          <ToastContainer
            position="top-right"
            autoClose={3000}
            newestOnTop={true}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="light"
          />
        </div>
      </BrowserRouter>
    </MyUserContext.Provider>
  );
};

export default App;

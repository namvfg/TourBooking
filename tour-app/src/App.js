import logo from './logo.svg';
import cookies from 'react-cookies';
import './App.css';
import { Container } from 'react-bootstrap';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import { MyUserContext } from './components/configs/Context';
import { useEffect, useState } from 'react';
import { authApis, endpoints } from './components/configs/Apis';
import Home from './components/Home';
import { useReducer } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import MyUserReducer from './components/reducers/MyUserReducer';
import Login from './components/user/Login';
import Register from './components/user/Register';
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ProtectedRoute from './components/layout/ProtectedRoute';
import Profile from './components/user/Profile';
import Unauthorized from './components/others/Unauthorized';

const App = () => {

  const [user, dispatch] = useReducer(MyUserReducer, undefined);
  const [isLoadingUser, setIsLoadingUser] = useState(true);

  const getUserFromToken = async () => {
    const token = cookies.load("token");

    if (token) {
      try {
        let userRes = await authApis(token).get(endpoints["profile"]);
        let userData = userRes.data;

        if (userData.role === "PROVIDER") {
          try {
            let providerRes = await authApis(token).get(endpoints["provider-profile"]);
            let providerData = providerRes.data;

            userData = { ...userData, provider: providerData };
          } catch (err) {
            console.error("Failed to fetch provider profile:", err);
          }
        }

        dispatch({ type: "login", payload: userData });
      } catch (error) {
        console.error("Failed to fetch user profile with stored token:", error);
        dispatch({ type: "logout" });
      }
    }
    setIsLoadingUser(false);
  };

  useEffect(() => {
    getUserFromToken();
  }, []);

  if (isLoadingUser) return null;

  return (
    <MyUserContext.Provider value={[user, dispatch]}>

      <BrowserRouter>
        <div style={{
          display: "flex",
          flexDirection: "column",
          minHeight: "100vh"
        }}>
          <Header />

          <main style={{ flex: 1 }}>
            <Container className="py-4">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/unauthorized" element={<Unauthorized />} />

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
        </div >
      </BrowserRouter>

    </MyUserContext.Provider >
  );
}

export default App;

import { useEffect, useContext } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import cookies from "react-cookies";
import { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";

const AuthRedirector = ({ onLoaded }) => {
    const [user, dispatch] = useContext(MyUserContext);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const checkToken = async () => {
            const token = cookies.load("token");
            if (!token) {
                onLoaded();
                return;
            }

            try {
                const res = await authApis(token).get(endpoints["profile"]);
                let userData = res.data;

                if (userData.role === "PROVIDER") {
                    try {
                        const providerRes = await authApis(token).get(endpoints["provider-profile"]);
                        userData = { ...userData, provider: providerRes.data };
                    } catch (e) {
                        console.error("Lỗi lấy provider profile", e);
                    }
                }

                dispatch({ type: "login", payload: userData });

                if (["/login", "/register", "/"].includes(location.pathname)) {
                    if (userData.role === "PROVIDER") {
                        navigate("/provider-home");
                    } else if (userData.role === "ADMIN") {
                        navigate("/profile");
                    } else {
                        navigate("/"); 
                    }
                }
            } catch (err) {
                console.error("Lỗi kiểm tra token:", err);
                dispatch({ type: "logout" });
            } finally {
                onLoaded();
            }
        };

        checkToken();
    }, []);

    return null;
};

export default AuthRedirector;

import React, { useContext } from "react";
import ServicePostList from "./ServicePostList";
import { MyUserContext } from './configs/Context';
import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const [user] = useContext(MyUserContext);
    const navigate = useNavigate();

    return (
        <div>
            {user && user.role === "PROVIDER" && (
                <Button variant="primary" onClick={() => navigate("/add-service-post")}>
                    Thêm dịch vụ
                </Button>
            )}
            <ServicePostList user={user} />
        </div>
    );
}

export default Home;
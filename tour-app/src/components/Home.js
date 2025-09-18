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
            <ServicePostList user={user} />
        </div>
    );
}

export default Home;
import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyDomUHutovBIzA-jefWODdPod_XpH4NPOA",
  authDomain: "tourappchat-ac46d.firebaseapp.com",
  projectId: "tourappchat-ac46d",
  storageBucket: "tourappchat-ac46d.appspot.com",
  messagingSenderId: "198504881267",
  appId: "1:198504881267:web:a1479594f084c29ef95e06",
  measurementId: "G-J4CRZYCBTG"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

export { db };
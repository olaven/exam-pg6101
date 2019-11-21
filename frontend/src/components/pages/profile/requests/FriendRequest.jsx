import * as React from "react";
import {UserContext} from "../../../context/UserContext";
import {FriendContext} from "../../../context/FriendContext";
import {PendingRequests} from "./PendingRequests";
import {SendButton} from "./SendButton";

export const FriendRequest = props => {

   /*
   * if this is me -> any pending friend requests to me? -> buttons
   * if this is not me
   *     are we already friends?
   *     have I sent? if not, button
   * */
   const {checkIfFriends} = React.useContext(FriendContext);
   const [areFriends, setAreFriends] = React.useState(null);

   React.useEffect(() => {

      checkIfFriends(props.email).then(areFriends => {

         setAreFriends(areFriends)
      });
   }, []);

   const { auth } = React.useContext(UserContext);

   if (auth.name === props.email) {

      return <PendingRequests/>
   } else if (areFriends) {

      return <p>This is a friend :-)</p>
   } else {

      return <SendButton email={props.email}/>
   }
};
import * as React from "react";
import {CardContent, CardHeader} from "semantic-ui-react";

export const MovieCard = props => <CardContent>

    <CardHeader>{props.movie.title}</CardHeader>
</CardContent>;
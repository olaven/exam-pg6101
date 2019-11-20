import * as React from "react";
import {Menu, MenuItem} from "semantic-ui-react";
import {Link} from "react-router-dom";

export const Links = props => <Menu>
    {props.pages.map(page => props.exclusions.includes(page.name)? null:
        <MenuItem key={page.name}>
            <Link to={page.path}>{page.name}</Link>
        </MenuItem>
    )}
</Menu>;


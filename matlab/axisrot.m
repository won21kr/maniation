function newvec = axisrot(vec, axis, angle)
    % Rotate a vector vec by angle around an arbitrary axis.

    axis = normalize(axis);
    x = axis(1);
    y = axis(2);
    z = axis(3);
    c = cos(angle/2);
    s = sin(angle/2);

    M= [c*c + s*s*( x*x - y*y - z*z), ...
        2*s*s*x*y + 2*c*s*z, ...
        2*s*s*x*z - 2*c*s*y; ...
        2*s*s*x*y - 2*c*s*z, ...
        c*c + s*s*(-x*x + y*y - z*z), ...
        2*s*s*y*z + 2*c*s*x; ...
        2*s*s*x*z + 2*c*s*y, ...
        2*s*s*y*z - 2*c*s*x, ...
        c*c + s*s*(-x*x - y*y + z*z)];

    newvec = M*vec;

endfunction
